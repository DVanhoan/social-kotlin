<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Foundation\Auth\User as Authenticatable;
use Illuminate\Notifications\Notifiable;
use Tymon\JWTAuth\Contracts\JWTSubject;

class User extends Authenticatable implements JWTSubject
{
    use HasFactory, Notifiable;

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
        return $this->hasMany(Post::class);
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
        $friendsFromSent = $this->sentFriendRequests()->where('status', 'accepted')->with('receiver')->get()->pluck('receiver');
        $friendsFromReceived = $this->receivedFriendRequests()->where('status', 'accepted')->with('requester')->get()->pluck('requester');
        return $friendsFromSent->merge($friendsFromReceived);
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