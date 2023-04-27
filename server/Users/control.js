import USERS from '../Auth/model.js';
import ApiView from '../common/apiview.js';

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

	return res.status(status).json({ msg, data });
};

export const UserInfo = async (req, res) => {
	const API = new ApiView(USERS, { is_blocked: false, _id: req.user._id }, {}, '-is_blocked -password -role'),
		{ status, msg, data } = await API.exec(API.detail());

	return res.status(status).json({ msg, data });
};
