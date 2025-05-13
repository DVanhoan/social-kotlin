<?php

namespace App\Http\Controllers;

use App\Models\Notification;
use Illuminate\Http\Request;
use App\Models\Comment;
use Illuminate\Support\Facades\Auth;

class CommentController extends Controller
{

    public function getCommentsOnPost($postId)
    {
        $comments = Comment::with('user')->where('post_id', $postId)->get();
        return response()->json($comments, 200);
    }


    public function comment(Request $request)
    {

        $data = $request->only(['comment', 'post', 'user_id', 'username']);

        $auth = Auth::guard('api')->user();

        $comment = Comment::create([
            'text'         => $data['comment'],
            'post_id'      => $data['post'],
            'user_id'      => $auth->id,
            'username'     => $auth->username,
            'comment_time' => now()
        ]);

        return response()->json($comment, 201);
    }
}
