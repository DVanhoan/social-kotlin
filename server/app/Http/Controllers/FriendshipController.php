<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Friendship;
use Illuminate\Support\Facades\Auth;

class FriendshipController extends Controller
{

    public function addFriend($userId, Request $request)
    {
        $currentUserId = Auth::id();
        $friendship = Friendship::create(attributes: [
            'user1_id' => $currentUserId,
            'user2_id' => $userId,
            'status'   => 'pending',
            'since'    => now()
        ]);
        return response()->json($friendship, 201);
    }

    public function acceptFriendRequest($userId, Request $request)
    {
        $currentUserId = Auth::id();
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
        $currentUserId = $request->input('current_user_id');
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
        $currentUserId = Auth::id();
        $pending = Friendship::where('user2_id', $currentUserId)
            ->where('status', 'pending')
            ->get();
        return response()->json($pending, 200);
    }


    public function getListOfFriends(Request $request)
    {
        $currentUserId = Auth::id();
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
