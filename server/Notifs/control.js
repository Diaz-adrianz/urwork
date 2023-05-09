import NOTIFS from './model.js';
import ApiView from '../common/apiview.js';

export const ListNotifs = async (req, res) => {
	const API = new ApiView(NOTIFS, { user_id: req.user._id }, req.query),
		{ status, msg, data } = await API.list(
			req.query.search,
			['title', 'message'],
			req.query.start,
			req.query.end,
			req.query.page
		);

	return res.status(status).json({ msg, ...data });
};

export const ReadNotif = async (req, res) => {
	const API = new ApiView(NOTIFS, { _id: req.params.key, user_id: req.user._id, is_read: false }),
		{ status, msg, data } = await API.update({ is_read: true });

	return res.status(status).json({ msg, data });
};
