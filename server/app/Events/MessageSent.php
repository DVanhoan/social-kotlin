<?php

namespace App\Events;

use Illuminate\Broadcasting\Channel;
use Illuminate\Contracts\Broadcasting\ShouldBroadcast;
use Illuminate\Foundation\Events\Dispatchable;
use Illuminate\Queue\SerializesModels;

class MessageSent implements ShouldBroadcast
{
    use Dispatchable, SerializesModels;

    public $message;

    public function __construct($message)
    {
        $this->message = $message;
    }


    public function broadcastOn()
    {
        return new Channel('conversation.' . $this->message->conversation_id);

        // return new PrivateChannel('conversation.' . $this->message->conversation_id);
    }

    public function broadcastAs()
    {
        return 'message.sent';
    }

    public function broadcastWith()
    {
        return [
            'id'              => $this->message->id,
            'content'         => $this->message->content,
            'sender_id'       => $this->message->sender_id,
            'conversation_id' => $this->message->conversation_id,
            'created_at'      => $this->message->created_at->format('H:i A'),
        ];
    }
}
