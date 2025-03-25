<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Post extends Model
{
    use HasFactory;

    protected $fillable = [
        'user_id',
        'main_photo',
        'content',
        'location',
        'posting_time',
        'deleted'
    ];

    protected $casts = [
        'deleted' => 'boolean',
        'posting_time' => 'datetime:Y-m-d H:i:s',
    ];



    public function user()
    {
        return $this->belongsTo(User::class, 'user_id');
    }


    public function comments()
    {
        return $this->hasMany(Comment::class);
    }


    public function reactions()
    {
        return $this->hasMany(Reaction::class);
    }

    public function getDeletedAttribute($value)
    {
        return (bool) $value;
    }

}
