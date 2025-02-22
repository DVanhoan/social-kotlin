<?php

namespace App\Http\Controllers;

use App\Like;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

class LikeController extends Controller
{
    public function like($post_id)
    {
        try {
            $user = Auth::user();
            $data = ['user_id' => $user->id, 'post_id' => $post_id];
            $alreadyExists = Like::where('user_id', $user->id)
                                  ->where('post_id', $post_id)
                                  ->exists();

            if ($alreadyExists) {
                return response("Bạn đã thích bài viết này", 400);
            }

            $like = Like::create($data);
            return response($like, 201);
        } catch (\Exception $e) {
            return response([
                'error' => $e->getMessage(),
                'message' => 'Có lỗi xảy ra khi thích bài viết này',
            ], 500);
        }
    }

    public function unlike($post_id)
    {
        try {
            $user = Auth::user();
            $unlike = Like::where('user_id', $user->id)
                          ->where('post_id', $post_id)
                          ->delete();

            return response(['message' => 'Đã bỏ thích bài viết'], 201);
        } catch (\Exception $e) {
            return response([
                'error' => $e->getMessage(),
                'message' => 'Có lỗi xảy ra khi bỏ thích bài viết này',
            ], 500);
        }
    }
}
