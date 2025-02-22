<?php

namespace App\Http\Controllers;

use App\Comment;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

class CommentController extends Controller
{
    public function insert(Request $request)
    {
        try {
            $body = $request->validate([
                'post_id' => 'required|integer',
                'body' => 'required|string',
            ]);

            $body['user_id'] = Auth::user()->id;
            $comment = Comment::create($body);

            return response($comment, 201);
        } catch (\Exception $e) {
            return response([
                'error' => $e->getMessage(),
                'message' => 'Có lỗi xảy ra khi đăng bình luận',
            ], 500);
        }
    }
}

