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

        $content = null;
        $imageUrl = null;
        $messageType = null;

        if ($request->hasFile('file')) {
            $file = $request->file('file');
            $cloudinaryUpload = Cloudinary::upload($file->getRealPath());
            $imageUrl = $cloudinaryUpload->getSecurePath();
        }

        if ($request->filled('content')) {
            $content = $request->input('content');
        }

        if (!$content && !$imageUrl) {
            return response()->json(['error' => 'Missing message content (text or file)'], 400);
        }


        if ($content && $imageUrl) {
            $messageType = 'text_image';
        } else if ($content) {
            $messageType = 'text';
        } else if ($imageUrl) {
            $messageType = 'image';
        }

        $message = Message::create([
            'content'         => $content,
            'sender_id'       => $user->id,
            'conversation_id' => $request->conversation_id,
            'message_type'    => $messageType,
            'image_url'       => $imageUrl,
        ]);

        $message->load('sender');

        $payload = [
            'id'              => $message->id,
            'isSender'        => true,
            'sender'          => $message->sender,
            'content'         => $message->content,
            'image_url'       => $message->image_url,
            'conversation_id' => $message->conversation_id,
            'created_at'      => $message->created_at->format('H:i A')
        ];

        broadcast(new MessageSent($message))->toOthers();
        Log:info('');
        return response()->json($payload, 200);
    }
}
