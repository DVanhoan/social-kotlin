@extends('admin.layouts.index')

@section('content')
    <style>
        .user-edit {
            width: 100%;
            max-width: 800px;
            margin: 20px auto;
            padding: 20px;
        }

        .user-edit h1 {
            color: #333;
            text-align: left;
        }

        .user-edit form {
            display: flex;
            flex-direction: column;
        }

        .user-edit label {
            margin-bottom: 5px;
            color: #666;
            font-weight: bold;
        }

        .user-edit input[type="text"],
        .user-edit input[type="email"],
        .user-edit input[type="text"] {
            width: calc(100% - 20px);
            padding: 10px;
            margin-bottom: 10px;
            border: 1px solid #ccc;
            border-radius: 4px;
        }

        .user-edit button {
            background-color: #0056b3;
            color: white;
            padding: 12px 20px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            transition: background-color 0.3s;
        }

        .user-edit button:hover {
            background-color: #004085;
        }

        .alert {
            padding: 10px 20px;
            margin: 10px 0;
            border-radius: 5px;
            color: white;
            text-align: center;
        }

        .alert-danger {
            background-color: #dc3545;
        }

        .alert-success {
            background-color: #28a745;
        }
    </style>

    <div class="user-edit">
        <h1>Edit User</h1>
        <form action="{{ route('admin.users.update', $user->id) }}" method="POST">
            @csrf
            @method('PUT')
            <label for="name">Name:</label>
            <input type="text" id="name" name="name" value="{{ $user->name }}" required>
            <label for="email">Email:</label>
            <input type="email" id="email" name="email" value="{{ $user->email }}" required>
            <label for="phone">Phone:</label>
            <input type="text" id="phone" name="phone" value="{{ $user->phone }}">
            <label for="address">Address:</label>
            <input type="text" id="address" name="address" value="{{ $user->address }}">
            <label for="image">Image:</label>
            <input type="text" id="image" name="image" value="{{ $user->image }}">
            <button type="submit">Update User</button>
        </form>
    </div>

    @if ($errors->any())
        <div class="alert alert-danger">
            <ul>
                @foreach ($errors->all() as $error)
                    <li>{{ $error }}</li>
                @endforeach
            </ul>
        </div>
    @endif

    @if (session('success'))
        <div class="alert alert-success">
            {{ session('success') }}
        </div>
    @endif

@endSection
