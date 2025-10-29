<?php

namespace App\Http\Controllers;

use App\Events\PostCreated;
use Illuminate\Http\Request;
use App\Models\Post;
use CloudinaryLabs\CloudinaryLaravel\Facades\Cloudinary;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Log;

class PostController extends Controller
{

    public function getPostsFromFriends()
    {
        $user = Auth::guard('api')->user();
        if (!$user) {
            return response()->json(['error' => 'Unauthorized'], 401);
        }
        $friendsIds = $user->friends->pluck('id')->toArray();

        if (empty($friendsIds)) {
            return response()->json([], 200);
        }

        $posts = Post::with('user')
            ->whereIn('user_id', $friendsIds)
            ->where('deleted', false)
            ->orderBy('posting_time', 'desc')
            ->get();

        return response()->json($posts, 200);
    }


    public function getAllPosts()
    {
        $userId = auth()->id();

        $posts = Post::with('user')

            ->withCount(['reactions', 'comments'])

            ->with(['reactions' => function($q) use ($userId) {
                $q->where('user_id', $userId);
            }])
            ->where('deleted', false)
            ->orderBy('posting_time', 'desc')
            ->get()
            ->map(function($post) {
                $userReaction = optional($post->reactions->first())->reaction_type;
                return [
                    'id'             => $post->id,
                    'user'           => $post->user,
                    'main_photo'      => $post->main_photo,
                    'content'        => $post->content,
                    'location'       => $post->location,
                    'deleted'        => $post->deleted,
                    'posting_time'    => $post->posting_time->format('Y-m-d H:i:s'),
                    'reactionCount'  => $post->reactions_count,
                    'commentCount'   => $post->comments_count,
                    'userReaction'   => $userReaction,
                ];
            });

        if ($posts->isEmpty()) {
            return response()->json(['message' => 'No posts found'], 404);
        }
        return response()->json($posts, 200);
    }




    public function createPost(Request $request)
    {
        if (!$request->hasFile('mainPhoto')) {
            return response()->json(['error' => 'Files missing'], 400);
        }
        $mainPhoto = $request->file('mainPhoto');
        if (!$mainPhoto->isValid()) {
            return response()->json(['error' => 'Invalid files'], 400);
        }

        $auth = Auth::guard('api')->user();
        try {
            $upload = Cloudinary::upload($mainPhoto->getRealPath());
            $url = $upload->getSecurePath();
        } catch (\Exception $e) {
            return response()->json(['error' => 'Upload failed'], 500);
        }

        $post = Post::create([
            'user_id'      => $auth->id,
            'main_photo'   => $url,
            'content'      => $request->input('content'),
            'location'     => $request->input('location'),
            'posting_time' => now(),
            'deleted'      => false,
        ]);

        $post->load('user');

        $resp = [
            'id'            => $post->id,
            'user'          => $post->user,
            'main_photo'    => $post->main_photo,
            'content'       => $post->content,
            'location'      => $post->location,
            'deleted'       => $post->deleted,
            'posting_time'  => $post->posting_time->format('Y-m-d H:i:s'),
            'reactionCount' => 0,
            'commentCount'  => 0,
            'userReaction'  => null,
        ];

        return response()->json($resp, 201);
    }



    public function addDescription(Request $request, $postId)
    {
        $description = $request->query('description');
        $post = Post::find($postId);
        if (!$post) {
            return response()->json(['error' => 'Post not found'], 404);
        }
        $post->description = $description;
        $post->save();
        return response()->json($post, 200);
    }
}
