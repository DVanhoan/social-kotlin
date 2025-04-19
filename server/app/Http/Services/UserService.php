<?php

namespace App\Http\Services;

use App\Models\User;
use Illuminate\Support\Facades\Hash;

class UserService
{
    private User $user;

    public function __construct(User $user)
    {
        $this->user = $user;
    }

    public function checkLogin($email, $password)
    {
        $user = $this->user->where('email', $email)->first();
        if ($user && Hash::check($password, $user->password)) {
            return $user;
        }
        return false;
    }

    public function register($request)
    {
        return $this->user->create([
            'username' => $request->username,
            'email' => $request->email,
            'password' => Hash::make($request->password),
            'role' => 'admin'
        ]);
    }

    public function create($request)
    {
        $userCreated = $this->user->create([
            'username' => $request->username,
            'fullName' => $request->fullName,
            'email' => $request->email,
            'biography' => $request->biography,
            'location' => $request->location,
            'profile_picture' => $request->profile_picture,
            'registration_date' => $request->registration_date,
            'password' => Hash::make($request->password),
            'role' => 'admin'
        ]);
        return $userCreated;
    }

    public function update($request, $id)
    {
        $user = $this->user->find($id);
        $user->username = $request->username;
        $user->email = $request->email;
        $user->password = Hash::make($request->password);
        $user->save();
        return $user;
    }

    public function delete($id)
    {
        $user = $this->getById($id);
        $user->delete();
        return $user;
    }

    public function getAll()
    {
        return $this->user->all();
    }

    public function getById($id)
    {
        return $this->user->find($id);
    }

    public function getByEmail($email)
    {
        return $this->user->where('email', $email)->first();
    }

    public function paginate($int)
    {
        return $this->user->paginate($int);
    }

    public function find($id)
    {
        return $this->user->find($id);
    }

    public function orderBy($column = 'id', $direction = 'asc')
    {
        return $this->user->orderBy($column, $direction);
    }
}
