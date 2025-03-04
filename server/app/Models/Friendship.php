<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Friendship extends Model
{
    use HasFactory;

    protected $fillable = [
        'user1_id',
        'user2_id',
        'status',
        'since'
    ];


    public function requester()
    {
        return $this->belongsTo(User::class, 'user1_id');
    }


    public function receiver()
    {
        return $this->belongsTo(User::class, 'user2_id');
    }
}
