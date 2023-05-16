import axios from 'axios';
import jwt from 'jsonwebtoken';
import bcrypt from 'bcrypt';

import USERS from './model.js';
import ApiView from '../common/apiview.js';
import { handleErrors, randomStr } from '../common/helpers.js';

export const Gauth = async (req, res) => {
	const { googleToken } = req.body;

	if (!googleToken) {
		return res.status(400).json({
			status: 'warning',
			msg: 'Something wrong with google auth',
		});
	}

	try {
		const gUser = await axios.get('https://www.googleapis.com/oauth2/v3/userinfo', {
			headers: {
				Authorization: `Bearer ${googleToken}`,
			},
		});

		const { given_name, family_name, email, picture } = gUser.data;

		const existUser = await USERS.findOne({ email });

		let uId, action, firstname, photo;

		if (!existUser) {
			const createUser = await USERS.create({
				first_name: given_name,
				last_name: family_name,
				email,
				photo: picture,
			});

			uId = createUser._id;
			firstname = createUser.first_name;
			photo = createUser.photo;
			action = 'Sign up';
		} else {
			(uId = existUser._id), (firstname = existUser.first_name), (photo = existUser.photo), (action = 'Sign in');
		}

		const token = jwt.sign(
			{
				_id: uId,
			},
			process.env.JWT_SECRET_KEY,
			{
				expiresIn: process.env.JWT_AGE_DAY + 'd',
			}
		);

		res.cookie('token', token, {
			httpOnly: true,
			sameSite: 'None',
			secure: true,
			maxAge: 1000 * 60 * 60 * 24 * parseInt(process.env.JWT_AGE_DAY),
		});

		res.status(200).json({
			msg: action + ' with google success',
			data: { _id: uId, first_name: firstname, photo: photo, last_name: token },
		});
	} catch (error) {
		let err = handleErrors(error);

		res.status(err.status).json({
			msg: err.msg,
		});
	}
};

export const Register = async (req, res) => {
	try {
		const { first_name, last_name, email, password } = req.body,
			API = new ApiView(USERS, { email });

		const user = await API.exec(API.detail());
		if (user.status == 200) return res.status(user.status).json({ msg: user.msg });

		const salt = await bcrypt.genSalt(10),
			hashedPass = await bcrypt.hash(password, salt),
			newUser = {
				first_name,
				last_name,
				email,
				password: hashedPass,
			},
			{ status, msg, data } = await API.create(newUser);

		if (status != 200) return res.status(status).json({ msg });

		delete data['password'];

		return res.status(status).json({ msg: 'Register success', data });
	} catch (error) {
		let err = handleErrors(error);

		res.status(err.status).json({
			msg: err.msg,
		});
	}
};

export const Login = async (req, res) => {
	try {
		const { email, password } = req.body,
			API = new ApiView(USERS, { email });

		const user = await API.exec(API.detail());
		if (user.status != 200) return res.status(user.status).json({ msg: user.msg, data: {} });

		const passwordOk = await bcrypt.compare(password, user.data.password);
		if (!passwordOk) return res.status(400).json({ msg: 'Invalid password', data: {} });

		const token = jwt.sign(
			{
				_id: user.data._id,
			},
			process.env.JWT_SECRET_KEY,
			{
				expiresIn: process.env.JWT_AGE_DAY + 'd',
			}
		);

		res.cookie('token', token, {
			httpOnly: true,
			sameSite: 'None',
			maxAge: 1000 * 60 * 60 * 24 * parseInt(process.env.JWT_AGE_DAY),
		});

		return res.status(user.status).json({
			msg: 'Login success',
			data: { _id: user.data._id, first_name: user.data.first_name, photo: user.data.photo, last_name: token },
		});
	} catch (error) {
		let err = handleErrors(error);

		res.status(err.status).json({
			msg: err.msg,
		});
	}
};

export const Logout = async (req, res) => {
	res.clearCookie('token');

	res.status(200).json({
		msg: 'Logout success',
	});
};
