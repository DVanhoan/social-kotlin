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
            'post_id'    => 'required|integer',
            'reaction' => 'required|string|in:happy,sad,angry,wow,haha,love'
        ]);

        $user = auth()->user();

        $reaction = Reaction::create([
            'user_id'       => $user->id,
            'post_id'       => $request->input('post_id'),
            'reaction_type' => $request->input('reaction'),
            'reaction_time' => now()
        ]);

        return response()->json($reaction, 201);
    }

}
