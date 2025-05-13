@extends('layout.index')

@section('content')
    <div class="main-content-inner">
        <div class="main-content-wrap">

            <div class="flex items-center flex-wrap justify-between gap20 mb-27">
                <h3>Create Post</h3>
                <ul class="breadcrumbs flex items-center flex-wrap justify-start gap10">
                    <li>
                        <a href="{{route('admin')}}"><div class="text-tiny">Dashboard</div></a>
                    </li>
                    <li>
                        <i class="icon-chevron-right"></i>
                    </li>
                    <li>
                        <a href="{{route('posts')}}"><div class="text-tiny">Posts</div></a>
                    </li>
                    <li>
                        <i class="icon-chevron-right"></i>
                    </li>
                    <li>
                        <div class="text-tiny">Edit Post</div>
                    </li>
                </ul>
            </div>


            <form
                action="{{ route('posts.update', ['postId' => $post->id]) }}"
                method="POST"
                enctype="multipart/form-data"
                class="tf-section-2 form-edit-post"
            >
                @csrf
                @method('PUT')

                <div class="wg-box">
                    <fieldset>
                        <div class="body-title mb-10">Content <span class="tf-color-1">*</span></div>
                        <textarea
                            name="content"
                            class="mb-10 ht-150"
                            required
                            maxlength="2000"
                            placeholder="Enter post content"
                        >{{ old('content', $post->content) }}</textarea>
                    </fieldset>

                    <fieldset>
                        <div class="body-title mb-10">Location</div>
                        <input
                            type="text"
                            name="location"
                            class="mb-10"
                            value="{{ old('location', $post->location) }}"
                            placeholder="Enter location"
                            maxlength="255"
                        >
                    </fieldset>

                    <fieldset>
                        <div class="body-title mb-10">Posting Time</div>
                        <input
                            type="datetime-local"
                            name="posting_time"
                            class="mb-10"
                            value="{{ old('posting_time', optional($post->posting_time)->format('Y-m-d\TH:i')) }}"
                        >
                    </fieldset>
                </div>

                <div class="wg-box">
                    <fieldset>
                        <div class="body-title mb-10">Main Photo</div>
                        <div class="upload-image flex-grow">
                            @if($post->main_photo)
                                <div class="item" id="imgpreview">
                                    <img src="{{ $post->main_photo }}" class="effect8" alt="">
                                </div>
                            @endif
                            <div id="upload-file" class="item up-load">
                                <label for="main_photo" class="uploadfile">
                                    <span class="icon"><i class="icon-upload-cloud"></i></span>
                                    <span class="body-text">
                                        Drop image here or <span class="tf-color">click to browse</span>
                                    </span>
                                    <input type="file" id="main_photo" name="main_photo" accept="image/*">
                                </label>
                            </div>
                        </div>
                    </fieldset>

                    <div class="cols gap10">
                        <button type="submit" class="tf-button w-full">Update Post</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
@endsection

@push('scripts')
    <script>
        $(function() {
            $('#main_photo').on('change', function() {
                const [file] = this.files;
                if (file) {
                    $('#imgpreview img').attr('src', URL.createObjectURL(file));
                    $('#imgpreview').show();
                }
            });
        });
    </script>
@endpush
