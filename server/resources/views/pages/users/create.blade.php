@extends('admin.layouts.index')

@section('content')
    <style>
        .user-create {
            width: 100%;
            max-width: 600px;
            margin: 20px auto;
            padding: 20px;
        }

        .user-create h1 {
            color: #333;
            text-align: center;
        }

        .user-create form {
            display: flex;
            flex-direction: column;
        }

        .user-create label {
            margin-bottom: 5px;
            color: #666;
            font-weight: bold;
        }

        .user-create input {
            padding: 10px;
            margin-bottom: 10px;
            border: 1px solid #ccc;
            border-radius: 4px;
        }

        .user-create button {
            background-color: #0056b3;
            color: white;
            padding: 12px 20px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            transition: background-color 0.3s;
        }

        .user-create button:hover {
            background-color: #004085;
        }
    </style>

    <div class="user-create">
        <h1>Add New User</h1>
        <form action="{{ route('admin.users.store') }}" method="POST">
            @csrf
            <label for="name">Name:</label>
            <input type="text" id="name" name="name" required>
            <label for="email">Email:</label>
            <input type="email" id="email" name="email" required>
            <label for="password">Password:</label>
            <input type="password" id="password" name="password" required>
            <button type="submit">Add User</button>
        </form>
    </div>

    @if (session('success'))
        <div class="alert alert-success">
            {{ session('success') }}
        </div>
    @elseif (session('error'))
        <div class="alert alert-danger">
            {{ session('error') }}
        </div>
    @endif

@endsection
