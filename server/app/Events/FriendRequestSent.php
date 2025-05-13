<?php

namespace App\Events;

use Illuminate\Broadcasting\PrivateChannel;
use Illuminate\Contracts\Broadcasting\ShouldBroadcast;
use Illuminate\Queue\SerializesModels;

class FriendRequestSent implements ShouldBroadcast
{
    use SerializesModels;

    public $receiverId;
    public $data;

    public function __construct($sender, $receiver)
    {
        $this->receiverId = $receiver->id;
        $this->data = [
            'sender_name' => $sender->fullName,
            'sender_id'   => $sender->id,
        ];
    }

    public function broadcastOn()
    {
        return new PrivateChannel('users.'.$this->receiverId);
    }

    public function broadcastAs()
    {
        return 'FriendRequestSent';
    }
}
