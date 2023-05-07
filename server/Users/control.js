import USERS from '../Auth/model.js';
import ApiView from '../common/apiview.js';
// import { cloudinary } from '../middlewares/fileuploader.js';
import { v2 as cloudinary } from 'cloudinary';

export const ListUser = async (req, res) => {
	const API = new ApiView(
			USERS,
			{ is_blocked: false },
			req.query,
			'-email -password -role -is_blocked -createdAt -updatedAt -__v'
		),
		{ status, msg, data } = await API.exec(
			API.list(
				req.query.search,
				['first_name', 'last_name', 'about', 'institute'],
				req.query.start,
				req.query.end,
				req.query.page
			)
		);

	return res.status(status).json({ msg, ...data });
};

export const UserInfo = async (req, res) => {
	const API = new ApiView(USERS, { is_blocked: false, _id: req.user._id }, {}, '-is_blocked -password -role'),
		{ status, msg, data } = await API.exec(API.detail());

	return res.status(status).json({ msg, data });
};

export const UpdateProfile = async (req, res) => {
	const API = new ApiView(USERS, { _id: req.user._id }),
		{ status, msg, data } = await API.exec(API.update({ ...req.body }));

	return res.status(status).json({ msg, data });
};

export const UpdatePhotoProfile = async (req, res) => {
	if (!req.file) return res.status(400).json({ msg: 'Image upload required' });

	const API = new ApiView(USERS, { _id: req.user._id }),
		userData = await API.exec(API.detail());

	if (userData.status != 200) {
		return res.status(userData.status).json({ msg: userData.msg });
	}

	if (userData.data.photo != '') {
		const oldPhoto = userData.data.photo,
			oldPhotoUrl = oldPhoto.split('/'),
			publicId = oldPhotoUrl[oldPhotoUrl.length - 1].split('.')[0];

		await cloudinary.uploader.destroy(publicId);
	}

	cloudinary.uploader
		.upload_stream({ folder: 'urwork' }, async (err, result) => {
			if (result) {
				const { status, msg, data } = await API.exec(API.update({ photo: result.url }));

				return res.status(status).json({ msg });
			} else {
				console.log(err);
				return res.status(500).json({ msg: 'Something wrong with upload to cloud' });
			}
		})
		.end(req.file.buffer);
};
