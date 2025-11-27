export type EnvironmentType = 'Any' | 'Beach' | 'Hill Country' | 'City' | 'Wildlife' | 'Cultural';
export type AccommodationType = 'Any' |'Hotel' | 'Resort' | 'Villa' | 'Guest House' | 'Hostel';

export interface Accommodation {
  id: string;
  name: string;
  address: string;
  district: string;
  environmentType: EnvironmentType;
  accomodationType: AccommodationType;
  avgTempByMonthC: Record<number, number>;       // 1..12
  avgPrecipByMonthMm: Record<number, number>;    // 1..12
  bookingUrl?: string;
}

export interface Accommodation2 {
  id: string;
  name: string;
  address: string;
  district: string;
  environmentType: EnvironmentType;
  accomodationType: AccommodationType;
  avgTempByMonthC: Record<number, number>;       // 1..12
  avgPrecipByMonthMm: Record<number, number>;    // 1..12
  bookingUrl?: string;
  weatherStatus?: string;
}

export interface Accommodation3 {
  id: string;
  name: string;
  address: string;
  district: string;
  environmentType: EnvironmentType;
  accomodationType: AccommodationType;
  avgTempByMonthC: Record<number, number>;       // 1..12
  avgPrecipByMonthMm: Record<number, number>;    // 1..12
  bookingUrl?: string;
  weatherStatus?: string;
  avgCrimeRateByMonth: Record<number, number>; 
  avgAccidentRateByMonth: Record<number, number>; 
}
