<?php

namespace App\Http\Controllers;

use App\Events\FriendRequestSent;
use App\Models\User;
use Illuminate\Http\Request;
use App\Models\Friendship;
use Illuminate\Support\Facades\Auth;

class FriendshipController extends Controller
{

    public function addFriend($userId, Request $request)
    {
        $user_sender = Auth::guard('api')->user();
        $currentUserId = $user_sender->id;
        $friendship = Friendship::create(attributes: [
            'user1_id' => $currentUserId,
            'user2_id' => $userId,
            'status'   => 'pending',
            'since'    => now()
        ]);

        $user_receiving = User::find($userId);

        event(new FriendRequestSent($user_sender, $user_receiving));

        $data = [
            'message' => 'Gửi lời mời kết bạn cho bạn',
            'sender_id' => $user_sender->id,
            'sender_name' => $user_sender->fullName
        ];
        $user_receiving->notifications()->create([
            'type' => 'FriendRequestSent',
            'data' => json_encode($data),
            'notifiable_type' => 'App\Models\User',
            'notifiable_id' => 2,
        ]);

        return response()->json($friendship, 201);
    }

    public function acceptFriendRequest($userId, Request $request)
    {
        $user = Auth::guard('api')->user();
        $currentUserId = $user->id;
        $friendship = Friendship::where('user1_id', $userId)
            ->where('user2_id', $currentUserId)
            ->first();
        if (!$friendship) {
            return response()->json(['error' => 'Friend request not found'], 404);
        }
        $friendship->status = 'accepted';
        $friendship->save();
        return response()->json($friendship, 200);
    }


    public function rejectFriend($userId, Request $request)
    {
        $user = Auth::guard('api')->user();
        $currentUserId = $user->id;
        $friendship = Friendship::where('user1_id', $userId)
            ->where('user2_id', $currentUserId)
            ->first();
        if (!$friendship) {
            return response()->json(['error' => 'Friend request not found'], 404);
        }
        $friendship->status = 'declined';
        $friendship->save();
        return response()->json(true, 200);
    }


    public function getPendingRequests(Request $request)
    {
        $user = Auth::guard('api')->user();
        $currentUserId = $user->id;
        $pending = Friendship::where('user2_id', $currentUserId)
            ->where('status', 'pending')
            ->get();
        return response()->json($pending, 200);
    }


    public function getSentRequests()
    {
        $user = Auth::guard('api')->user();
        $sent = Friendship::where('user1_id', $user->id)
            ->where('status', 'pending')
            ->get();
        return response()->json($sent, 200);
    }



    public function getListOfFriends()
    {
        $user = Auth::guard('api')->user();
        $currentUserId = $user->id;
        $friends1 = Friendship::where('user1_id', $currentUserId)
            ->where('status', 'accepted')
            ->pluck('user2_id');
        $friends2 = Friendship::where('user2_id', $currentUserId)
            ->where('status', 'accepted')
            ->pluck('user1_id');
        $friends = $friends1->merge($friends2);
        return response()->json($friends->values(), 200);
    }
}
