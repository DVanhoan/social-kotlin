<?php

namespace App\Http\Services;

use App\Models\Notification as NotificationModel;
use Illuminate\Support\Facades\Auth;
use Illuminate\Database\Eloquent\Collection;

class NotificationService
{
    private NotificationModel $notification;

    public function __construct(NotificationModel $notification)
    {
        $this->notification = $notification;
    }


    public function create(string $type, array $data, int $notifiableId, string $notifiableType): NotificationModel
    {
        $userId = Auth::id();

        $notification = $this->notification->create([
            'user_id'           => $userId,
            'type'              => $type,
            'notifiable_id'     => $notifiableId,
            'notifiable_type'   => $notifiableType,
            'data'              => json_encode($data),
        ]);

        return $notification;
    }


    public function all(bool $unreadOnly = false, int $limit = 50): Collection
    {
        $query = $this->notification
            ->where('user_id', Auth::id())
            ->orderByDesc('created_at');

        if ($unreadOnly) {
            $query->whereNull('read_at');
        }

        return $query->limit($limit)->get();
    }


    public function markAsRead(int $notificationId): bool
    {
        $notification = $this->notification
            ->where('id', $notificationId)
            ->where('user_id', Auth::id())
            ->first();

        if (! $notification) {
            return false;
        }

        $notification->read_at = now();
        return $notification->save();
    }


    public function markAllAsRead(): int
    {
        return $this->notification
            ->where('user_id', Auth::id())
            ->whereNull('read_at')
            ->update(['read_at' => now()]);
    }


    public function unreadCount(): int
    {
        return $this->notification
            ->where('user_id', Auth::id())
            ->whereNull('read_at')
            ->count();
    }
}
