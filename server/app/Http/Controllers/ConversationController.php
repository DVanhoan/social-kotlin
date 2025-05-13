<?php

namespace App\Http\Controllers;

use App\Models\Conversation;
use App\Http\Controllers\Controller;
use Illuminate\Http\Request;

class ConversationController extends Controller
{
    public function index()
    {
        $user = auth()->user();

        if (!$user) {
            return response()->json(['message' => 'User not authenticated'], 401);
        }

        $conversations = $user->conversations()
            ->with([
                'latestMessage' => function ($query) {
                    $query->latest('updated_at');
                },
                'members.user'
            ])
            ->get()
            ->sortByDesc(function ($conversation) {
                return optional($conversation->latestMessage)->updated_at;
            })
            ->map(function ($conversation) use ($user) {
                $otherParticipant = $conversation->getOtherParticipantAttribute();

                return [
                    'id' => $conversation->id,
                    'name' => $conversation->name,
                    'last_message' => $conversation->latestMessage ? $conversation->latestMessage->content : null,
                    'last_message_time' => $conversation->latestMessage ? $conversation->latestMessage->updated_at->format('H:i A') : null,
                    'isSender' => $conversation->latestMessage && $conversation->latestMessage->sender_id === $user->id,
                    'other_participant' => $otherParticipant
                        ? [
                            'id' => $otherParticipant->id,
                            'username' => $otherParticipant->username,
                            'profile_picture' => $otherParticipant->profile_picture,
                        ]
                        : null,
                ];
            })
            ->values()
            ->toArray();


        return response()->json([
            'conversations' => $conversations
        ]);
    }


    public function create(Request $request)
    {
        $user = auth()->user();

        if (!$user) {
            return response()->json(['message' => 'User not authenticated'], 401);
        }

        $validatedData = $request->validate([
            'name' => 'required|string',
            'type' => 'required|string',
            'members' => 'required|array',
            'members.*' => 'integer|exists:users,id'
        ]);


        $conversation = Conversation::create([
            'name' => $validatedData['name'],
            'type' => $validatedData['type'],
        ]);


        $conversation->users()->attach($user->id, [
            'is_admin'  => true,
            'joined_at' => now()
        ]);


        foreach ($validatedData['members'] as $memberId) {
            $conversation->users()->attach($memberId, [
                'is_admin'  => false,
                'joined_at' => now()
            ]);
        }

        return response()->json([
            'status'       => 'success',
            'conversationId' => $conversation->id
        ]);
    }



    public function show($id)
    {
        $user = auth()->user();
        if (!$user) {
            return response()->json(['message' => 'User not authenticated'], 401);
        }

        $conversation = $user->conversations()
            ->with(['members.user'])
            ->find($id);

        if (!$conversation) {
            return response()->json(['message' => 'Conversation not found'], 404);
        }

        $messages = $conversation
            ->messages()
            ->with('sender')
            ->orderBy('created_at', 'asc')
            ->get()
            ->map(function ($message) {
                return [
                    'id'         => $message->id,
                    'isSender'   => $message->sender_id === auth()->id(),
                    'sender'     => $message->sender,
                    'content'     => $message->content,
                    'image_url'  => $message->image_url,
                    'created_at'  => $message->created_at->format('H:i A'),
                    'conversation_id' => $message->conversation_id
                ];
            });

        $members = $conversation->members->map(function ($member) {
            return [
                'id'              => $member->user->id,
                'username'        => $member->user->username,
                'profile_picture' => $member->user->profile_picture ?: '',
            ];
        });

        return response()->json([
            'id'       => $conversation->id,
            'type'     => $conversation->type,
            'name'     => $conversation->name,
            'messages' => $messages,
            'members'  => $members,
        ]);
    }
}
