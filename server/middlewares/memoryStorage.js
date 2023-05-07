import multer from 'multer';
import path from 'path';
const storage = multer.memoryStorage();

const UPLOAD = multer({
	storage,
	limits: { fileSize: 2000000 },
	fileFilter: (req, file, cb) => {
		const filetypes = /jpeg|jpg|png/,
			mimetype = filetypes.test(file.mimetype),
			extName = filetypes.test(path.extname(file.originalname).toLowerCase());

		if (mimetype && extName) {
			return cb(null, Date.now() + file.originalname);
		}

		cb('Only image format allowed');
	},
});

export default UPLOAD;
