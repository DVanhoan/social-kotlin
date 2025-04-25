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
                        <div class="text-tiny">Create Post</div>
                    </li>
                </ul>
            </div>


            <form class="tf-section-2 form-add-post" method="POST" enctype="multipart/form-data" action="{{route('posts.store')}}">
                @csrf
                <div class="wg-box">

                    <fieldset class="name">
                        <div class="body-title mb-10">Post Content <span class="tf-color-1">*</span></div>
                        <textarea class="mb-10 ht-150" name="content" placeholder="Write your post content..." tabindex="0" aria-required="true"></textarea>
                        @error("content") <span class="alert alert-danger text-center">{{$message}}</span> @enderror
                    </fieldset>


                    <fieldset class="name">
                        <div class="body-title mb-10">Location</div>
                        <input type="text" placeholder="Enter location" name="location" value="{{ old('location') }}">
                        @error("location") <span class="alert alert-danger text-center">{{$message}}</span> @enderror
                    </fieldset>


                    <fieldset>
                        <div class="body-title">Main Photo <span class="tf-color-1">*</span></div>
                        <div class="upload-image flex-grow">
                            <div class="item" id="imgpreview" style="display:none">
                                <img src="#" class="effect8" alt="Preview">
                            </div>
                            <div id="upload-file" class="item up-load">
                                <label class="uploadfile" for="mainPhoto">
                                    <span class="icon">
                                        <i class="icon-upload-cloud"></i>
                                    </span>
                                    <span class="body-text">Drop your image here or <span class="tf-color">click to browse</span></span>
                                    <input type="file" id="mainPhoto" name="main_photo" accept="image/*" required>
                                </label>
                            </div>
                        </div>
                        @error("main_photo") <span class="alert alert-danger text-center">{{$message}}</span> @enderror
                    </fieldset>


                    <div class="cols gap22">
                        <fieldset class="name">
                            <div class="body-title mb-10">Posting Time</div>
                            <input type="datetime-local" name="posting_time" value="{{ old('posting_time', now()->format('Y-m-d\TH:i')) }}">
                            @error("posting_time") <span class="alert alert-danger text-center">{{$message}}</span> @enderror
                        </fieldset>
                    </div>


                    <div class="cols gap10">
                        <button class="tf-button w-full" type="submit">Create Post</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
@endsection

@push("scripts")
    <script>
        $(function(){
            $("#mainPhoto").on("change", function(e){
                const [file] = this.files;
                if (file) {
                    $("#imgpreview img")
                        .attr('src', URL.createObjectURL(file))
                        .on('load', () => URL.revokeObjectURL(this.src));
                    $("#imgpreview").show();
                }
            });

            flatpickr("input[type=datetime-local]", {
                enableTime: true,
                dateFormat: "Y-m-d H:i",
                time_24hr: true
            });
        });
    </script>
@endpush
