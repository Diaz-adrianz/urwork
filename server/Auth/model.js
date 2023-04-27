import mongoose from 'mongoose';
import paginate from 'mongoose-paginate-v2';

const { model, Schema } = mongoose;

const schema = new Schema(
	{
		first_name: String,
		last_name: String,
		email: { type: String, unique: true },
		password: { type: String, default: '' },
		about: { type: String, default: '' },
		photo: { type: String, default: '' },
		role: {
			type: String,
			enum: ['admin', 'user'],
			default: 'user',
		},
		institute: { type: String, default: '' },
		is_blocked: { type: Boolean, default: false },
	},
	{ timestamps: true }
);

schema.plugin(paginate);
const USERS = model('users', schema);

export default USERS;
