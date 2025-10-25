export type EnvironmentType = 'Any' | 'Beach' | 'Hill Country' | 'City' | 'Wildlife' | 'Cultural';
export type AccommodationType = 'Any' |'Hotel' | 'Resort' | 'Villa' | 'Guest House' | 'Hostel';

export interface Accommodation {
  id: string;
  name: string;
  address: string;
  district: string;
  environment: EnvironmentType;
  type: AccommodationType;
  avgTempByMonthC: Record<number, number>;       // 1..12
  avgPrecipByMonthMm: Record<number, number>;    // 1..12
  bookingUrl?: string;
}
