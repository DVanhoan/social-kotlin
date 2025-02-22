<?php

namespace App\Http\Controllers;

use App\Post;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use CloudinaryLabs\CloudinaryLaravel\Facades\Cloudinary;

class PostController extends Controller
{
    public function uploadPost(Request $request)
    {
        try {
            $request->validate(['image' => 'required|image']);
            $image_path = Cloudinary::uploadFile($request->file('image')->getRealPath())->getSecurePath();

            $user = Auth::user();
            $body = [
                'image' => $image_path,
                'description' => $request->description,
                'user_id' => $user->id
            ];

            $post = Post::create($body);
            return response($post);
        } catch (\Exception $e) {
            return response(['error' => $e,], 500);
        }
    }

    public function getFeed()
    {
        try {
            $user = Auth::user();
            $userIds = $user->followings()->pluck('followed_id');
            $userIds->push($user->id);

            return Post::whereIn('user_id', $userIds)
                ->with('user', 'likes', 'comments')
                ->latest()
                ->get();
        } catch (\Exception $e) {
            return response([
                'error' => $e->getMessage(),
                'message' => 'Có lỗi xảy ra khi tải lên feed',
            ], 500);
        }
    }

    public function getById($id)
    {
        try {
            return Post::find($id)->load('user', 'likes', 'comments');
        } catch (\Exception $e) {
            return response([
                'error' => $e->getMessage(),
                'message' => 'Có lỗi xảy ra khi lấy bài viết',
            ], 500);
        }
    }

    public function deletePost($id)
    {
        try {
            $post = Post::find($id);

            if ($post) {
                $post->delete();
                return response([
                    'message' => 'Bài viết đã được xóa thành công',
                    'post' => $post
                ]);
            } else {
                return response([
                    'message' => 'Bài viết không tồn tại',
                ], 404);
            }
        } catch (\Exception $e) {
            return response([
                'error' => $e->getMessage(),
                'message' => 'Có lỗi xảy ra khi xóa bài viết',
            ], 500);
        }
    }
}
