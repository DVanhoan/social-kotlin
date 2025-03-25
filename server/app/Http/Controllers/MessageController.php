<?php

namespace App\Http\Controllers;

use App\Http\Controllers\Controller;
use App\Models\Message;
use Illuminate\Http\Request;
use App\Events\MessageSent;
use CloudinaryLabs\CloudinaryLaravel\Facades\Cloudinary;

class MessageController extends Controller
{
    public function send(Request $request)
    {
        $user = auth()->user();
        if (!$user) {
            return response()->json(['message' => 'User not authenticated'], 401);
        }

        $request->validate([
            'conversation_id' => 'required|integer',
        ]);

        $messageType = '';
        $content = '';


        if ($request->hasFile('content')) {
            $file = $request->file('content');
            $cloudinaryUpload = Cloudinary::upload($file->getRealPath());
            $url = $cloudinaryUpload->getSecurePath();
            $content = $url;
            $messageType = 'image';
        }
        else if ($request->filled('content')) {
            $content = $request->input('content');
            $messageType = 'text';
        } else {
            return response()->json(['error' => 'Missing message content (text or file)'], 400);
        }

        $message = Message::create([
            'content'         => $content,
            'sender_id'       => $user->id,
            'conversation_id' => $request->conversation_id,
            'message_type'    => $messageType,
        ]);

        broadcast(new MessageSent($message))->toOthers();

        return response()->json([
            'status'  => 'success',
            'message' => $message,
        ]);
    }
}
