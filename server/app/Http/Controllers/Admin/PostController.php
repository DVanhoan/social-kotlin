<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\Post;
use CloudinaryLabs\CloudinaryLaravel\Facades\Cloudinary;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Log;
use RealRashid\SweetAlert\Facades\Alert;

class PostController extends Controller
{
    public function index()
    {
        $posts = Post::withCount('comments')->orderBy('id', 'ASC')->paginate(10);
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

            Alert('success', 'Post created successfully');
            return redirect()->route('posts');
        } catch (\Exception $e) {
            Alert('error', 'Failed to create post');
            return back()->withInput()->with('error', 'Failed to create post');
        }
    }


    public function edit($postId)
    {
        $post = Post::findOrFail($postId);

        return view('pages.posts.edit', compact('post'));
    }


    public function update(Request $request, Post $post)
    {
        $validated = $request->validate([
            'content'      => 'required|string|max:2000',
            'location'     => 'nullable|string|max:255',
            'posting_time' => 'nullable|date',
            'main_photo'   => 'sometimes|image|max:5120'
        ]);

        try {
            // Chuẩn bị dữ liệu update
            $data = [
                'content'      => $validated['content'],
                'location'     => $validated['location'],
                'posting_time' => $validated['posting_time'] ?? now(),
            ];

            // Nếu có upload ảnh mới, thêm vào mảng data
            if ($request->hasFile('main_photo')) {
                $uploadedFile = Cloudinary::upload(
                    $request->file('main_photo')->getRealPath()
                );
                $data['main_photo'] = $uploadedFile->getSecurePath();
            }

            $post->update($data);

            Alert::success('Success', 'Post updated successfully');
            return redirect()->route('posts');
        } catch (\Exception $e) {
            Log::error('Post update failed: ' . $e->getMessage());
            Alert::error('Error', 'Failed to update post');
            return back()->withInput();
        }
    }



    public function destroy(Post $post)
    {
        try {
            $post->update(['deleted' => true]);
            Alert::success('Success', 'Post deleted successfully');
            return redirect()->route('posts');
        } catch (\Exception $e) {
            Alert::error('Error', 'Failed to delete post');
            return back()->with('error', 'Failed to delete post');
        }
    }

    public function showComment(Post $post)
    {
        $comments = $post->comments()->paginate(10);
        return view('pages.comment.index', compact('comments', 'post'));
    }
}
