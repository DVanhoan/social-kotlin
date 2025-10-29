<?php

namespace App\Http\Controllers;

use App\Models\Post;
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
        $data = $request->validate([
            'post_id'  => 'required|exists:posts,id',
            'reaction' => 'required|string|in:like,sad,tired,wow,haha,love',
        ]);

        $user = auth()->user();


        $reaction = Reaction::where('user_id', $user->id)
            ->where('post_id', $data['post_id'])
            ->first();


        if ($reaction) {
            $reaction->update([
                'reaction_type' => $data['reaction'],
                'reaction_time' => now()
            ]);
        } else {
            $reaction = Reaction::create([
                'user_id'       => $user->id,
                'post_id'       => $data['post_id'],
                'reaction_type' => $data['reaction'],
                'reaction_time' => now()
            ]);
        }

        $reaction->load('user');

        $reactionCount = Reaction::where('post_id', $data['post_id'])->count();
        $reaction->reaction_count = $reactionCount;

        return response()->json($reaction, 200);
    }


}
