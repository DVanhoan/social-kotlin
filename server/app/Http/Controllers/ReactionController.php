<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Reaction;
use CloudinaryLabs\CloudinaryLaravel\Facades\Cloudinary;

class ReactionController extends Controller
{

    public function getReactionsOnPost($postId)
    {
        $reactions = Reaction::with('user')->where('post_id', $postId)->get();
        return response()->json($reactions, 200);
    }


    public function react(Request $request)
    {
        $request->validate([
            'user_id' => 'required|integer',
            'post'    => 'required|integer',
            'reaction' => 'required|string|in:happy,sad,angry,wow,haha,love'
        ]);

        $reaction = Reaction::create([
            'user_id'       => $request->input('user_id'),
            'post_id'       => $request->input('post'),
            'reaction_type' => $request->input('reaction'),
            'reaction_time' => now()
        ]);

        return response()->json($reaction, 201);
    }

}
