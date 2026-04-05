CREATE UNIQUE INDEX IF NOT EXISTS idx_seat_date
ON bookings (seat_no, booked_date)
WHERE UPPER(TRIM(status)) <> 'CANCELLED';

CREATE UNIQUE INDEX IF NOT EXISTS unique_user_booking_per_day
ON bookings (booked_by, DATE(booked_at));

CREATE UNIQUE INDEX IF NOT EXISTS idx_user_date_active
ON bookings (booked_by, booked_date)
WHERE UPPER(TRIM(status)) <> 'CANCELLED';

ALTER TABLE assets
ADD CONSTRAINT asset_restriction_check CHECK (
    UPPER(TRIM(status)) <> 'AVAILABLE'
    OR owner IS NULL
);