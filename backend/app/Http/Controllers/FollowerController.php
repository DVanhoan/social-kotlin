<?php
namespace App\Http\Controllers;

use App\Follower;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

class FollowerController extends Controller
{
    public function follow($followed_id)
    {
        try {
            $user = Auth::user();
            $data = ['follower_id' => $user->id, 'followed_id' => $followed_id];
            $follow = Follower::create($data);
            return response($follow, 201);
        } catch (\Exception $e) {
            return response([
                'error' => $e->getMessage(),
                'message' => 'Có lỗi xảy ra khi theo dõi người dùng này',
            ], 500);
        }
    }

    public function unfollow($followed_id)
    {
        try {
            $user = Auth::user();
            $unfollow = Follower::where('follower_id', $user->id)
                                ->where('followed_id', $followed_id)
                                ->delete();
            return response(['message' => 'Đã hủy theo dõi thành công'], 201);
        } catch (\Exception $e) {
            return response([
                'error' => $e->getMessage(),
                'message' => 'Có lỗi xảy ra khi hủy theo dõi người dùng này',
            ], 500);
        }
    }
}
