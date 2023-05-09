import mongoose from 'mongoose';
import paginate from 'mongoose-paginate-v2';

const { model, Schema } = mongoose;

const schema = new Schema(
	{
		title: { type: String, maxlength: 50, required: true },
		description: { type: String, required: true },
		author: {
			type: Schema.Types.ObjectId,
			ref: 'users',
		},
		collaborators: [{ type: Schema.Types.ObjectId, ref: 'users' }],
		images: [String],
		tags: String,
		stars: [{ type: Schema.Types.ObjectId, ref: 'users' }],
		duration_start: { type: Date, default: '' },
		duration_end: { type: Date, default: '' },
		completed_date: { type: Date, default: '' },
		is_blocked: { type: Boolean, default: false },
	},
	{ timestamps: true }
);

schema.plugin(paginate);
const PROJECTS = model('projects', schema);

export default PROJECTS;
