<?php

namespace App\Events;

use Illuminate\Broadcasting\PrivateChannel;
use Illuminate\Contracts\Broadcasting\ShouldBroadcast;
use Illuminate\Queue\SerializesModels;
use App\Models\Post;

class PostCreated implements ShouldBroadcast
{
    use SerializesModels;

    public $friendIds;
    public $data;

    public function __construct(Post $post, array $friendIds)
    {
        $this->friendIds = $friendIds;
        $this->data = [
            'post_id'   => $post->id,
            'title'     => $post->title,
            'author_id' => $post->user_id,
        ];
    }

    public function broadcastOn()
    {
        return array_map(fn($id) => new PrivateChannel('users.' . $id), $this->friendIds);
    }

    public function broadcastAs()
    {
        return 'PostCreated';
    }
}
