@extends('layout.index')

@section('content')
<div class="main-content-inner">
    <div class="main-content-wrap">
        <div class="flex items-center flex-wrap justify-between gap20 mb-27">
            <h3>All Posts</h3>
            <ul class="breadcrumbs flex items-center flex-wrap justify-start gap10">
                <li>
                    <a href="{{route('admin')}}">
                        <div class="text-tiny">Dashboard</div>
                    </a>
                </li>
                <li>
                    <i class="icon-chevron-right"></i>
                </li>
                <li>
                    <div class="text-tiny">All Posts</div>
                </li>
            </ul>
        </div>

        <div class="wg-box">
            <div class="flex items-center justify-between gap10 flex-wrap">
                <div class="wg-filter flex-grow">
                    <form class="form-search">
                        <fieldset class="name">
                            <input type="text" placeholder="Search content..." class="" name="search"
                                value="{{ request('search') }}" aria-required="true">
                        </fieldset>
                        <div class="button-submit">
                            <button type="submit"><i class="icon-search"></i></button>
                        </div>
                    </form>
                </div>
                <a class="tf-button style-1 w208" href="{{route('posts.create')}}"><i class="icon-plus"></i>Add New</a>
            </div>
            <div class="table-responsive">
                <table class="table table-striped table-bordered">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Content</th>
                            <th>Author</th>
                            <th>Location</th>
                            <th>Posting Time</th>
                            <th>comments</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        @foreach($posts as $post)
                        <tr>
                            <td>{{ $post->id }}</td>
                            <td class="pname">
                                <div class="image">
                                    <img src="{{ asset($post->main_photo) }}" alt="Post image" class="image"
                                        style="max-width: 80px;">
                                </div>
                                <div class="name">
                                    <a href="#" class="body-title-2">{{ Str::limit($post->content, 40) }}</a>
                                </div>
                            </td>
                            <td>{{ $post->user->username }}</td>
                            <td>{{ $post->location ?? 'N/A' }}</td>
                            <td>{{ $post->posting_time->format('M d, Y H:i') }}</td>

                            <td>
                                <a href="{{ route('posts.showComment', $post->id) }}">
                                    <span class="text-tiny mt-3 text-primary">{{ $post->comments_count }}
                                        comments</span>
                                </a>
                            </td>
                            <td>
                                <span class="status-badge {{ $post->deleted ? 'deleted' : 'active' }}">
                                    {{ $post->deleted ? 'Deleted' : 'Active' }}
                                </span>
                            </td>
                            <td>
                                <div class="list-icon-function">
                                    {{-- <a href="{{ route('posts.show', $post->id) }}">--}}
                                        {{-- <div class="item eye">--}}
                                            {{-- <i class="icon-eye"></i>--}}
                                            {{-- </div>--}}
                                        {{-- </a>--}}
                                    <a href="{{ route('posts.edit', $post->id) }}">
                                        <div class="item edit">
                                            <i class="icon-edit-3"></i>
                                        </div>
                                    </a>
                                    <form action="{{ route('posts.destroy', $post->id) }}" method="POST">
                                        @csrf
                                        @method('DELETE')
                                        <div class="item text-danger delete">
                                            <i class="icon-trash-2"></i>
                                        </div>
                                    </form>

                                    @push('scripts')
                                    <script>
                                        $(function(){
                                                    $(".delete").on('click',function(e){
                                                        e.preventDefault();
                                                        var selectedForm = $(this).closest('form');
                                                        swal({
                                                            title: "Are you sure?",
                                                            text: "This post will be deleted permanently!",
                                                            type: "warning",
                                                            buttons: ["Cancel", "Confirm"],
                                                            confirmButtonColor: '#dc3545'
                                                        }).then(function (result) {
                                                            if (result) {
                                                                selectedForm.submit();
                                                            }
                                                        });
                                                    });
                                                });
                                    </script>
                                    @endpush
                                </div>
                            </td>
                        </tr>
                        @endforeach
                    </tbody>
                </table>
            </div>

            <div class="divider"></div>
            <div class="flex items-center justify-between flex-wrap gap10 wgp-pagination">
                {{ $posts->links("pagination::bootstrap-5") }}
            </div>
        </div>
    </div>
</div>
@endsection