import { v2 as cloudinary } from 'cloudinary';
import multer from 'multer';
import { CloudinaryStorage } from 'multer-storage-cloudinary';
import path from 'path';

// cloudinary.config({
// 	cloud_name: `${process.env.CLOUDINARY_CLOUD_NAME}`,
// 	api_secret: `${process.env.CLOUDINARY_API_SECRET}`,
// 	api_key: `${process.env.CLOUDINARY_API_KEY}`,
// });

const storage = new CloudinaryStorage({
	cloudinary,
	params: {
		allowedFormats: ['jpg', 'png', 'jpeg'],
		folder: 'urwork',
		max_bytes: 2000000,
	},
});

export { cloudinary, storage };
