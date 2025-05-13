@extends('layout.index')

@section('content')

    <div class="main-content-inner">
        <div class="main-content-wrap">
            <div class="flex items-center flex-wrap justify-between gap20 mb-27">
                <h3>Dashboard</h3>
            </div>

            <div class="flex row">
                <div class="col-3 gap20 ">
                    <div class="card">
                        <div class="card-header">
                            <div class="card-title">Total Users</div>
                        </div>
                        <div class="card-body">
                            <div class="text-center">
                                <h1>{{ $total_users }}</h1>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="col-3 gap20">
                    <div class="card">
                        <div class="card-header">
                            <div class="card-title">Total Posts</div>
                        </div>
                        <div class="card-body">
                            <div class="text-center">
                                <h1>{{ $total_posts }}</h1>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="col-3 gap20">
                    <div class="card">
                        <div class="card-header">
                            <div class="card-title">Total Comments</div>
                        </div>
                        <div class="card-body">
                            <div class="text-center">
                                <h1>{{ $total_comments }}</h1>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="col-3 gap-20">
                    <div class="card">
                        <div class="card-header">
                            <div class="card-title">Total Reaction</div>
                        </div>
                        <div class="card-body">
                            <div class="text-center">
                                <h1>{{ $total_reactions }}</h1>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>



        <div class="flex items-center flex-wrap justify-between gap20">
            <div class="col-6 card w-full">

                <div class="card-header">
                    <div class="card-title">Top 10 user ranking</div>
                </div>

                <div class="card-body">
                    <table class="table table-striped table-bordered">
                        <thead>
                        <tr>
                            <th>#</th>
                            <th>Name</th>
                            <th>Posts</th>
                            <th>Comments</th>
                            <th>Reactions</th>
                        </tr>
                        </thead>

                        <tbody>

                        @foreach ($user_with_most_posts as $user)
                            <tr>
                                <td>{{ $loop->iteration }}</td>
                                <td class="pname">
                                    <div class="image">
                                        <img src="{{ $user->profile_picture }}" alt="Profile Picture" class="image">
                                    </div>
                                    <div class="name">
                                        <a href="#" class="body-title-2">{{ $user->username }}</a>
                                        <div class="text-tiny mt-3">{{ $user->fullName }}</div>
                                    </div>
                                </td>
                                <td>{{ $user->posts_count }}</td>
                                <td>{{ $user->comments_count }}</td>
                                <td>{{ $user->reactions_count }}</td>
                            </tr>

                        @endforeach
                        </tbody>

                    </table>
                </div>

            </div>



            <div class="col-6 card w-full">
                <div class="card-header">
                    <div class="card-title">Top 10 post ranking</div>
                </div>

                <div class="card-body">

                    <table class="table table-striped table-bordered">
                        <thead>
                        <tr>
                            <th>#</th>
                            <th>Content</th>
                            <th>Posting time</th>
                            <th>Comments</th>
                            <th>Reactions</th>
                        </tr>
                        </thead>

                        <tbody>

                        @foreach ($post_with_most_comments_and_reactions as $post)

                            <tr>
                                <td>{{ $loop->iteration }}</td>
                                <td class="pname">
                                    <div class="image">
                                        <img src="{{ $post->main_photo }}" alt="main photo" class="image">
                                    </div>
                                    <div class="name">
                                        <a href="#" class="body-title-2">{{ $post->content }}</a>
                                    </div>
                                </td>
                                <td>{{ $post->posting_time }}</td>
                                <td>{{ $post->comments_count }}</td>
                                <td>{{ $post->reactions_count }}</td>
                            </tr>

                        @endforeach

                </div>
            </div>
        </div>
    </div>
@endsection
