import mongoose from 'mongoose';
import paginate from 'mongoose-paginate-v2';

const { model, Schema } = mongoose;

const schema = new Schema(
	{
		user_id: { type: Schema.Types.ObjectId, ref: 'users' },
		title: String,
		message: String,
		is_read: { type: Boolean, default: false },
	},
	{ timestamps: true }
);

schema.plugin(paginate);
const NOTIFS = model('notifs', schema);

export default NOTIFS;
