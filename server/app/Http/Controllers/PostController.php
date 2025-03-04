<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Post;
use CloudinaryLabs\CloudinaryLaravel\Facades\Cloudinary;
use Illuminate\Support\Facades\Auth;

class PostController extends Controller
{
    public function getImage($filename)
    {
        $path = Cloudinary::path($filename);
        if ($path) {
            return response()->file($path);
        }
        return response()->json(['error' => 'Image not found'], 404);
    }

    public function canUserPost()
    {
        return response()->json(true, 200);
    }

    public function getPostsFromFriends()
    {
        $user = Auth::user();
        if (!$user) {
            return response()->json(['error' => 'Unauthorized'], 401);
        }
        $friendsIds = $user->friends->pluck('id')->toArray();
        $posts = Post::whereIn('user_id', $friendsIds)->get();
        if ($posts->isEmpty()) {
            return response()->json(['error' => 'No posts found'], 404);
        }
        return response()->json($posts, 200);
    }

    public function getTodaysPostByUser($userId)
    {
        $today = date('Y-m-d');
        $post = Post::where('user_id', $userId)
            ->whereDate('posting_time', $today)
            ->first();
        if ($post) {
            return response()->json($post, 200);
        }
        return response()->json(['error' => 'No post found for today'], 404);
    }


    public function createPost(Request $request)
    {

        if (!$request->hasFile('mainPhoto') || !$request->hasFile('selfiePhoto')) {
            return response()->json(['error' => 'Files missing'], 400);
        }
        $mainPhoto = $request->file('mainPhoto');
        $selfiePhoto = $request->file('selfiePhoto');

        if (!$mainPhoto->isValid() || !$selfiePhoto->isValid()) {
            return response()->json(['error' => 'Invalid files'], 400);
        }

        $mainPhotoName = Cloudinary::upload($mainPhoto->getRealPath())->getSecurePath();
        $selfiePhotoName = Cloudinary::upload($selfiePhoto->getRealPath())->getSecurePath();

        $post = Post::create([
            'user_id'      => $request->input('user_id'),
            'username'     => $request->input('username'),
            'main_photo'   => $mainPhotoName,
            'selfie_photo' => $selfiePhotoName,
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
