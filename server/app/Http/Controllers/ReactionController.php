<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Reaction;
use CloudinaryLabs\CloudinaryLaravel\Facades\Cloudinary;

class ReactionController extends Controller
{
    public function getReactionImage($filename)
    {
        $path = Cloudinary::path($filename);
        if ($path) {
            return response()->file($path);
        }
        return response()->json(['error' => 'Image not found'], 404);
    }


    public function getReactionsOnPost($postId)
    {
        $reactions = Reaction::where('post_id', $postId)->get();
        return response()->json($reactions, 200);
    }


    public function react(Request $request)
    {
        if (!$request->hasFile('reaction')) {
            return response()->json(['error' => 'No reaction image uploaded'], 400);
        }
        $file = $request->file('reaction');
        if (!$file->isValid()) {
            return response()->json(['error' => 'Invalid reaction image'], 400);
        }
        $filename = Cloudinary::upload($file->getRealPath())->getSecurePath();

        $reaction = Reaction::create([
            'user_id'       => $request->input('user_id'),
            'username'      => $request->input('username'),
            'post_id'       => $request->input('post'),
            'image_name'    => $filename,
            'reaction_time' => now()
        ]);
        return response()->json($reaction, 201);
    }
}
