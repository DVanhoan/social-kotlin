<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Comment;

class CommentController extends Controller
{

    public function getCommentsOnPost($postId)
    {
        $comments = Comment::where('post_id', $postId)->get();
        return response()->json($comments, 200);
    }


    public function comment(Request $request)
    {

        $data = $request->only(['comment', 'post', 'user_id', 'username']);
        $comment = Comment::create([
            'text'         => $data['comment'],
            'post_id'      => $data['post'],
            'user_id'      => $data['user_id'] ?? null,
            'username'     => $data['username'] ?? 'Anonymous',
            'comment_time' => now()
        ]);
        return response()->json($comment, 201);
    }
}
