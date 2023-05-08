import multer from 'multer';
import path from 'path';
const storage = multer.memoryStorage();

export const UPLOAD = multer({
	storage,
	limits: { fileSize: 2000000 },
	fileFilter: (req, file, cb) => {
		const filetypes = /jpeg|jpg|png/,
			mimetype = filetypes.test(file.mimetype),
			extName = filetypes.test(path.extname(file.originalname).toLowerCase());

		if (mimetype && extName) {
			return cb(null, Date.now() + file.originalname);
		}

		req.fileValidationError = 'Only image format allowed';

		cb(null, false);
	},
});

export const fileValidationIsError = (err, req, res, next) => {
	if (err instanceof multer.MulterError) {
		if (err.code === 'LIMIT_FILE_SIZE') {
			return res.status(400).json({ msg: 'File size exceeds limit', data: null });
		}
	}

	if (req.fileValidationError) {
		return res.status(400).json({ msg: req.fileValidationError, data: null });
	}

	next();
};
