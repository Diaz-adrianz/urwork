import mongoose from 'mongoose';

const mongoId = mongoose.Types.ObjectId;

export const mongoIdValidator = (id) => {
	if (mongoId.isValid(id)) {
		return String(new mongoId(id)) === id ? true : false;
	}

	return false;
};

export const arraySpaceRemover = (val) => {
	let newVal = '';

	for (let i = 0; i < val.length; i++) {
		if (val[i] == ' ') continue;
		newVal += val[i];
	}

	return newVal;
};

export const randomStr = (length) => {
	let result = '';
	const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
	const charactersLength = characters.length;
	let counter = 0;
	while (counter < length) {
		result += characters.charAt(Math.floor(Math.random() * charactersLength));
		counter += 1;
	}
	return result;
};

export const handleErrors = (err) => {
	if (err.name === 'NotFound') {
		return {
			status: 404,
			msg: (err.lookup || 'Data') + ' not found',
data: null
		};
	} else if (err.name === 'CastError' && err.kind === 'ObjectId') {
		return {
			status: 400,
			msg: 'Invalid identifier',
data: null
		};
	} else if (err.name === 'ValidationError') {
		let errors = [];

		Object.keys(err.errors).forEach((key) => {
			errors.push(err.errors[key].message);
		});

		return {
			status: 400,
			msg: errors[0],
data: null
		};
	} else if (err.name === 'MongoServerError' && err.code === 11000) {
		let errors = [];

		Object.keys(err.keyValue).forEach((key) => {
			errors.push(`${key} already exist`);
		});

		return {
			status: 422,
			msg: errors[0],
data: null
		};
	} else {
		console.log(err);
	}

	console.log('ERR: ' + err.message);

	return {
		status: 500,
		msg: 'Internal server error',
data: null
	};
};
