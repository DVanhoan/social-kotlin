<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Http\Services\CommentService;

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
        Alert('success', 'Comment deleted successfully');
        return redirect()->back()->with('success', 'Comment deleted successfully');
    }
}
