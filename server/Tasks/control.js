import moment from 'moment';

import TASKS from './model.js';
import ApiView from '../common/apiview.js';

export const ListTask = async (req, res) => {
	const filter = req.path == '/my' ? {} : { project_id: req.project?._id },
		API = new ApiView(TASKS, filter, req.query);

	API.addPopulate('project_id', 'title collaborators author');
	API.addPopulate('completed_by', '_id first_name');

	let { status, msg, data } = await API.exec(
		API.list(req.query.search, ['title'], req.query.start, req.query.end, req.query.page)
	);

	if (req.path == '/my') {
		data = data.filter((dat) => {
			return (
				(dat.project_id != null || dat.project_id != {}) &&
				(dat.project_id.author == req.user._id || dat.project_id.collaborators.includes(req.user._id))
			);
		});
	}

	return res.status(status).json({ msg, data });
};

export const CreateTask = async (req, res) => {
	const payload = {
			...req.body,
			project_id: req.project._id,
		},
		API = new ApiView(TASKS),
		{ status, msg, data } = await API.exec(API.create(payload));

	return res.status(status).json({ msg, data });
};

export const UpdateTask = async (req, res) => {
	const API = new ApiView(TASKS, { _id: req.params.key, project_id: req.project._id }),
		{ status, msg, data } = await API.exec(API.update({ ...req.body }));

	return res.status(status).json({ msg, data });
};

export const DeleteTask = async (req, res) => {
	const API = new ApiView(TASKS, { _id: req.params.key, project_id: req.project._id }),
		{ status, msg, data } = await API.exec(API.delete());

	return res.status(status).json({ msg, data });
};

export const CompleteTask = async (req, res) => {
	const API = new ApiView(TASKS, { _id: req.params.key, project_id: req.project._id });

	API.addPopulate('completed_by', 'first_name');

	const detail = await API.exec(API.detail());

	if (detail.status == 200 && (detail.data.completed_by != undefined || detail.data.completed_by != null)) {
		return res.status(200).json({
			msg: 'Task has completed by ' + detail.data.completed_by?.first_name,
		});
	}
	const { status, msg, data } = await API.exec(API.update({ completed_by: req.user._id, completed_date: moment() }));

	return res.status(status).json({ msg, data });
};
