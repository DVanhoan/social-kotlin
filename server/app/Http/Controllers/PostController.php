<?php

namespace App\Http\Controllers;

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
        $posts = Post::with('user')
        ->where('deleted', false)
            ->orderBy('posting_time', 'desc')
            ->get();

        if ($posts->isEmpty()) {
            return response()->json([
                'message' => 'No posts found',
            ]);
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
            $cloudinaryUpload = Cloudinary::upload($mainPhoto->getRealPath());
            $mainPhotoName = $cloudinaryUpload->getSecurePath();
            Log::info('Cloudinary upload success', ['url' => $mainPhotoName]);
        } catch (\Exception $e) {
            Log::error('Cloudinary upload failed', ['error' => $e->getMessage()]);
            return response()->json(['error' => 'Upload failed'], 500);
        }


        $post = Post::create([
            'user_id'      => $auth->id,
            'main_photo'   => $mainPhotoName,
            'content'      => $request->input('content'),
            'location'     => $request->input('location'),
            'posting_time' => now(),
            'deleted'      => false,
        ]);
        return response()->json($post, 201);
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
