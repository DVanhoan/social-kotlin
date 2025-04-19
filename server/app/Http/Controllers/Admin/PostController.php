<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\Post;
use CloudinaryLabs\CloudinaryLaravel\Facades\Cloudinary;
use Dom\Comment;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Log;

class PostController extends Controller
{
    public function index()
    {
        $posts = Post::withCount('comments')->orderBy('id', 'DESC')->paginate(10);
        return view('pages.posts.index', compact('posts'));
    }


    public function create()
    {
        return view('pages.posts.create');
    }


    public function store(Request $request)
    {
        $validated = $request->validate([
            'content' => 'required|string|max:2000',
            'location' => 'nullable|string|max:255',
            'posting_time' => 'nullable|date',
            'main_photo' => 'required|image|max:5120'
        ]);

        try {
            if ($request->file('mainPhoto')) {
                $uploadedFile = Cloudinary::upload($request->file('main_photo')->getRealPath());
                $mainPhotoUrl = $uploadedFile->getSecurePath();
            }


            Post::create([
                'user_id' => auth()->user()->id,
                'main_photo' => $mainPhotoUrl,
                'content' => $validated['content'],
                'location' => $validated['location'],
                'posting_time' => $validated['posting_time'] ?? now(),
                'deleted' => false
            ]);

            return redirect()->route('pages.posts.index')->with('success', 'Post created successfully');
        } catch (\Exception $e) {
            Log::error('Post creation failed: ' . $e->getMessage());
            return back()->withInput()->with('error', 'Failed to create post');
        }
    }


    public function edit(Post $post)
    {
        return view('pages.posts.edit', compact('post'));
    }


    public function update(Request $request, Post $post)
    {
        $validated = $request->validate([
            'content' => 'required|string|max:2000',
            'location' => 'nullable|string|max:255',
            'posting_time' => 'nullable|date',
            'main_photo' => 'sometimes|image|max:5120'
        ]);

        try {
            if ($request->hasFile('main_photo')) {
                $uploadedFile = Cloudinary::upload($request->file('main_photo')->getRealPath());
                $post->main_photo = $uploadedFile->getSecurePath();
            }

            $post->update([
                'content' => $validated['content'],
                'location' => $validated['location'],
                'posting_time' => $validated['posting_time'] ?? $post->posting_time
            ]);

            return redirect()->route('pages.posts.index')->with('success', 'Post updated successfully');
        } catch (\Exception $e) {
            Log::error('Post update failed: ' . $e->getMessage());
            return back()->withInput()->with('error', 'Failed to update post');
        }
    }


    public function destroy(Post $post)
    {
        try {
            $post->update(['deleted' => true]);
            return redirect()->route('pages.posts.index')->with('success', 'Post deleted successfully');
        } catch (\Exception $e) {
            Log::error('Post deletion failed: ' . $e->getMessage());
            return back()->with('error', 'Failed to delete post');
        }
    }

    public function showComment(Post $post)
    {
        $comments = $post->comments()->paginate(10);
        return view('pages.comment.index', compact('comments', 'post'));
    }
}
