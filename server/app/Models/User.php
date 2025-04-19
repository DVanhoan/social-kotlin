<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Foundation\Auth\User as Authenticatable;
use Illuminate\Notifications\Notifiable;
use Tymon\JWTAuth\Contracts\JWTSubject;

class User extends Authenticatable implements JWTSubject
{
    use HasFactory, Notifiable;


    protected $visible = [
        'id',
        'username',
        'fullName',
        'email',
        'biography',
        'location',
        'profile_picture',
        'registration_date',
        'role'
    ];

    protected $fillable = [
        'username',
        'password',
        'fullName',
        'email',
        'biography',
        'location',
        'profile_picture',
        'registration_date'
    ];

    protected $hidden = [
        'password'
    ];


    public function posts()
    {
        return $this->hasMany(Post::class, 'user_id');
    }


    public function comments()
    {
        return $this->hasMany(Comment::class);
    }


    public function reactions()
    {
        return $this->hasMany(Reaction::class);
    }


    public function sentFriendRequests()
    {
        return $this->hasMany(Friendship::class, 'user1_id');
    }


    public function receivedFriendRequests()
    {
        return $this->hasMany(Friendship::class, 'user2_id');
    }


    public function friends()
    {
        return $this->belongsToMany(User::class, 'friendships', 'user1_id', 'user2_id')
            ->wherePivot('status', 'accepted');
    }

    public function conversations()
    {
        return $this->belongsToMany(Conversation::class, 'conversation_members')
            ->withPivot('is_admin', 'joined_at')
            ->withTimestamps();
    }


    public function messages()
    {
        return $this->hasMany(Message::class, 'sender_id');
    }


    public function getJWTIdentifier()
    {
        return $this->getKey();
    }

    public function getJWTCustomClaims()
    {
        return [];
    }
}
