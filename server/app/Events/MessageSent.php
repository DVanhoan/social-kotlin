<?php

namespace App\Events;

use Illuminate\Broadcasting\Channel;
use Illuminate\Broadcasting\PrivateChannel;
use Illuminate\Contracts\Broadcasting\ShouldBroadcast;
use Illuminate\Foundation\Events\Dispatchable;
use Illuminate\Queue\SerializesModels;
use Illuminate\Support\Facades\Log;

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
        //        return new Channel('conversation.' . $this->message->conversation_id);

        Log::info('Broadcasting to: conversation.' . $this->message->conversation_id);
        return new Channel('chat.' . $this->message->conversation_id);
    }

    public function broadcastAs()
    {
        Log::info('Broadcasting as: message.sent');
        return 'MessageSent';
    }

    public function broadcastWith()
    {

        Log::info('Broadcasting with: ' . json_encode($this->message));
        return ['message' => $this->message];
    }
}
