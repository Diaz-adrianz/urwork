import { v2 as cloudinary } from 'cloudinary';

import PROJECTS from './model.js';
import ApiView from '../common/apiview.js';
import TASKS from '../Tasks/model.js';
import NOTIFS from '../Notifs/model.js';
import moment from 'moment';

export const ListProj = async (req, res) => {
	let filter = { is_blocked: false };

	if (req.path == '/my') {
		filter = {
			$or: [{ author: req.user._id }, { collaborators: { $in: [req.user._id] } }],
			is_blocked: false,
		};
	} else if (req.path == '/ongoing') {
		filter = {
			$or: [{ author: req.user._id }, { collaborators: { $in: [req.user._id] } }],
			duration_start: { $lte: moment() },
			duration_end: { $gte: moment() },
			is_blocked: false,
		};
	}
	const API = new ApiView(PROJECTS, filter, req.query, '');

	API.addPopulate('author', '_id first_name');
	API.addPopulate('collaborators', '_id first_name');

	const { status, msg, data } = await API.exec(
		API.list(req.query.search, ['title', 'tags'], req.query.start, req.query.end, req.query.page)
	);

	if (status == 200) {
		data.data = await Promise.all(
			data.data.map(async (dat) => {
				const obj = dat.toObject();
				const projectTasks = new ApiView(TASKS, { project_id: dat._id, completed_date: { $ne: null } }),
					completedTasks = await projectTasks.exec(projectTasks.count());

				projectTasks.filters = { project_id: dat._id, completed_date: null };
				const uncompletedTasks = await projectTasks.exec(projectTasks.count());

				if (uncompletedTasks.status == 200 && completedTasks.status == 200) {
					const totalTasks = completedTasks.data + uncompletedTasks.data;
					const percentage = (completedTasks.data / totalTasks) * 100;
					obj['percentage'] = percentage;
				}
				return obj;
			})
		);
	}

	return res.status(status).json({ msg, ...data });
};

export const DetailProj = async (req, res) => {
	const API = new ApiView(PROJECTS, { _id: req.params.key, is_blocked: false });

	API.addPopulate('author', 'first_name last_name about photo');
	API.addPopulate('collaborators', 'first_name last_name about photo');

	let { status, msg, data } = await API.exec(API.detail());

	if (status == 200) {
		data = data.toObject();

		const projectTasks = new ApiView(TASKS, { project_id: data._id, completed_date: { $ne: null } }),
			completedTasks = await projectTasks.exec(projectTasks.count());

		projectTasks.filters = { project_id: data._id, completed_date: null };
		const uncompletedTasks = await projectTasks.exec(projectTasks.count());

		if (uncompletedTasks.status == 200 && completedTasks.status == 200) {
			const totalTasks = completedTasks.data + uncompletedTasks.data;
			const percentage = (completedTasks.data / totalTasks) * 100;
			data['percentage'] = percentage;
		}
	}

	return res.status(status).json({ msg, data });
};

export const CreateProj = async (req, res) => {
	const payload = {
			...req.body,
			author: req.user._id,
			tags: req.body.tags.join(', '),
		},
		API = new ApiView(PROJECTS);

	const { status, msg, data } = await API.exec(API.create(payload));

	const APInotifs = new ApiView(NOTIFS);

	Promise.all(
		payload.collaborators.map(async (collaborator) => {
			await APInotifs.create({
				user_id: collaborator,
				title: 'Project invitation',
				message: `You are added in the '${payload.title}' project!`,
			});
		})
	);

	return res.status(status).json({ msg, data });
};

export const UpdateProj = async (req, res) => {
	const payload = {
			...req.body,
			tags: req.body.tags?.join(', '),
		},
		API = new ApiView(PROJECTS, { _id: req.params.key, author: req.user._id, is_blocked: false });

	const { status, msg, data } = await API.exec(API.update(payload));

	return res.status(status).json({ msg, data });
};

export const AddImages = async (req, res) => {
	if (!req.file) return res.status(400).json({ msg: 'Image upload required', data: null });

	const API = new ApiView(PROJECTS, { _id: req.params.key, author: req.user._id, is_blocked: false }),
		detail = await API.exec(API.detail());

	if (detail.status != 200) {
		return res.status(detail.status).json({ msg: detail.msg, data: detail.data });
	}

	if (detail.data.images.length == 3) {
		return res.status(400).json({ msg: 'Images exceed limit of 3', data: null });
	}

	try {
		await cloudinary.uploader
			.upload_stream({ folder: 'urwork' }, async (err, result) => {
				if (err) {
					console.error(err);
					throw err;
				}
				console.log(result);
				const { status, msg, data } = await API.exec(API.update({ $addToSet: { images: result.url } }));

				return res.status(status).json({ msg, data });
			})
			.end(req.file.buffer);
	} catch (error) {
		console.error(error);
		return res.status(500).json({ msg: 'Something wrong while upload image', data: null });
	} finally {
		if (req.file && req.file.buffer) {
			req.file.buffer = null;
		}
	}
};

export const RemoveImage = async (req, res) => {
	const image = req.body.image_url.split('/'),
		publicId = image[image.length - 1].split('.')[0],
		API = new ApiView(PROJECTS, { _id: req.params.key, author: req.user._id, is_blocked: false }),
		detail = await API.exec(API.detail());

	if (detail.status != 200) {
		return res.status(detail.status).json({ msg: detail.msg, data: detail.data });
	}

	console.log(publicId);

	try {
		const deleting = await cloudinary.uploader.destroy('urwork/' + publicId);

		if (deleting.result != 'ok') {
			return res.status(500).json({ msg: deleting.result, data: null });
		}
	} catch (error) {
		return res.status(400).json({ msg: 'Invalid image url', data: null });
	}

	const { status, msg, data } = await API.exec(API.update({ $pull: { images: req.body.image_url } }));

	return res.status(status).json({ msg, data });
};

export const DeleteProj = async (req, res) => {
	const API = new ApiView(PROJECTS, { _id: req.params.key }),
		{ status, msg, data } = await API.exec(API.delete());

	return res.status(status).json({ msg, data });
};

export const StarringProj = async (req, res) => {
	const option =
			req.params.option == 'no' ? { $pull: { stars: req.user._id } } : { $addToSet: { stars: req.user._id } },
		API = new ApiView(PROJECTS, { _id: req.params.key }),
		{ status, msg, data } = await API.exec(API.update({ ...option }));

	return res.status(status).json({ msg, data });
};
