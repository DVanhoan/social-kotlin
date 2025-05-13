<?php

namespace App\Http\Services;

use App\Models\Comment;
use GuzzleHttp\Psr7\Request;

class CommentService
{
    private Comment $comment;
    public function __construct(Comment $comment)
    {
        $this->comment = $comment;
    }


    public function insert(Request $request)
    {
        $this->comment->create($request->all());
    }

    public function update(Request $request)
    {
        $this->comment->update($request->all());
    }

    public function delete($id)
    {
        $this->comment->find($id)->delete();
    }

    public function getById($id)
    {
        return $this->comment->find($id);
    }

    public function find($id)
    {
        return $this->comment->find($id);
    }

    public function getAll()
    {
        return $this->comment->all();
    }

    public function paginate($int)
    {
        return $this->comment->paginate($int);
    }

    public function orderBy($column = 'id', $direction = 'asc')
    {
        return $this->comment->orderBy($column, $direction);
    }
}
