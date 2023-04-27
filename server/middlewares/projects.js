import PROJECTS from '../Projects/model.js';
import ApiView from '../common/apiview.js';

export const ProjectCheck = async (req, res, next) => {
	const identifier = req.params.project_id || req.params.key,
		API = new ApiView(PROJECTS, { _id: identifier }),
		{ status, msg, data } = await API.exec(API.detail());

	if (status != 200) {
		return res.status(status).json({ msg });
	}

	if (data.is_blocked) {
		return res.status(404).json({
			msg: 'The project has been blocked',
		});
	}

	if (!data.collaborators.includes(req.user._id) && data.author != req.user._id) {
		return res.status(403).json({
			msg: 'You are not a part of project',
		});
	}

	req.project = { _id: data._id, author: data.author };

	next();
};

export const IsAuthor = async (req, res, next) => {
	if (req.project.author != req.user._id) {
		return res.status(403).json({
			msg: 'You are not allowed',
		});
	}

	next();
};
