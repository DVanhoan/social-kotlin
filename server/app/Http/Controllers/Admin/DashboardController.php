<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\Comment;
use App\Models\Post;
use App\Models\Reaction;
use App\Models\User;

class DashboardController extends Controller
{
    public function index()
    {
        // get total number of users, posts, comments, reactions
        $total_users = User::count();
        $total_posts = Post::count();
        $total_comments = Comment::count();
        $total_reactions = Reaction::count();


        // top 10 users with most posts
        $user_with_most_posts = User::withCount(['posts', 'comments', 'reactions'])->orderBy('posts_count', 'desc')
            ->orderBy('comments_count', 'desc')->orderBy('reactions_count', 'desc')->limit(10)->get();


        // top 10 posts with most comments and reactions
        $post_with_most_comments_and_reactions = Post::withCount(['comments', 'reactions'])
            ->orderBy('comments_count', 'desc')->orderBy('reactions_count', 'desc')->limit(10)->get();

        // share data with view
        view()->share(compact('user_with_most_posts', 'post_with_most_comments_and_reactions'));


        // render view
        return view("pages.dashboard", compact('total_users', 'total_posts', 'total_comments', 'total_reactions'));
    }
}
