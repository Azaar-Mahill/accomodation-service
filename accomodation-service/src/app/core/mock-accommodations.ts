import { Accommodation } from './models';

export const ACCOMMODATIONS: Accommodation[] = [
  {
    id: 'ACC001',
    name: 'Accommodation1',
    address: 'Address1',
    district: 'Galle',
    environmentType: 'Beach',
    accomodationType: 'Resort',
    avgTempByMonthC: {1:27,2:27,3:28,4:28,5:28,6:27,7:27,8:27,9:27,10:27,11:27,12:27},
    avgPrecipByMonthMm: {1:50,2:60,3:120,4:250,5:350,6:400,7:250,8:200,9:180,10:250,11:300,12:150},
    bookingUrl: 'https://example.com/book/ACC001'
  },
  {
    id: 'ACC002',
    name: 'Accommodation2',
    address: 'Address2',
    district: 'Kandy',
    environmentType: 'Hill Country',
    accomodationType: 'Hotel',
    avgTempByMonthC: {1:22,2:22,3:23,4:24,5:24,6:23,7:23,8:23,9:23,10:23,11:22,12:22},
    avgPrecipByMonthMm: {1:70,2:80,3:110,4:160,5:200,6:210,7:160,8:150,9:160,10:220,11:230,12:120},
    bookingUrl: 'https://example.com/book/ACC002'
  },
  {
    id: 'ACC003',
    name: 'Accommodation3',
    address: 'Address3',
    district: 'Colombo',
    environmentType: 'City',
    accomodationType: 'Hotel',
    avgTempByMonthC: {1:28,2:29,3:30,4:30,5:29,6:28,7:28,8:28,9:28,10:28,11:28,12:28},
    avgPrecipByMonthMm: {1:60,2:70,3:140,4:240,5:310,6:360,7:230,8:180,9:170,10:240,11:290,12:160}
  },
  {
    id: 'ACC004',
    name: 'Accommodation4',
    address: 'Address4',
    district: 'Yala',
    environmentType: 'Wildlife',
    accomodationType: 'Villa',
    avgTempByMonthC: {1:26,2:26,3:27,4:28,5:29,6:28,7:28,8:28,9:27,10:27,11:26,12:26},
    avgPrecipByMonthMm: {1:40,2:50,3:70,4:120,5:140,6:110,7:70,8:50,9:60,10:120,11:180,12:90}
  }
];
