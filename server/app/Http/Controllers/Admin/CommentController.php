<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use CommentService;
use Dom\Comment;
use Illuminate\Http\Request;

class CommentController extends Controller
{
    private CommentService $commentService;
    public function __construct(CommentService $commentService)
    {
        $this->commentService = $commentService;
    }

    public function destroy($commentId)
    {
        $this->commentService->delete($commentId);
        return redirect()->back()->with('success', 'Comment deleted successfully');
    }
}